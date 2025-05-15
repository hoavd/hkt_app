import { useEffect, useRef, useState } from 'react';
import { useDispatch } from 'react-redux';
import { HashRouter } from 'react-router-dom';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';
import NotificationSound from './assets/audio/audio.mp3';
import './assets/styles/app.css';
import './assets/styles/app.scss';
import { ErrorBoundary } from './components/bundary/ErrorBundary';
import { BASE_URL_SOCKET, topicPath } from './config/apiPath';
import AppRouters from './config/appRouter';
import ContextProviderLayout from './contexts/ContextProvider';
import useNotify from './hooks/useNotify';
import i18n from './translations';
import { getLanguageStorage, getToken } from './utils/storage';
import { setAlert } from './redux/slice/appSlice';

let stompClient;

const App = () => {
  const languageStorage = getLanguageStorage();

  const audioPlayer = useRef();
  const notify = useNotify();
  const token = getToken();
  const dispatch = useDispatch();
  const [isError, setIsError] = useState(true);

  const connectServerGetData = () => {
    setIsError(false);
    const socket = new SockJS(BASE_URL_SOCKET);
    stompClient = Stomp.over(socket);
    stompClient.debug = true;
    stompClient.heartbeat = {
      outgoing: 0,
      incoming: 10000
    };
    stompClient.connect({ Authorization: 'Bearer ' + token }, onConnected, onError);
  };

  const onConnected = () => {
    stompClient.subscribe(topicPath.topicAlert, onMessageReceived);
  };

  const onError = (error) => {
    console.log(error);
    setTimeout(() => {
      setIsError(true);
    }, 5000);
  };

  const onMessageReceived = (payload) => {
    const message = JSON.parse(payload.body);
    if (payload.headers.destination === topicPath.topicAlert) {
      dispatch(setAlert(message));
      audioPlayer.current?.play().catch((err) => {
        console.warn('Audio play failed:', err);
      });
      if (message.data.type === 'error') {
        notify.error('Co error');
      } else {
        notify.warning('Co warning');
      }
    }
  };

  useEffect(() => {
    if (isError && token) {
      connectServerGetData();
    }
    // eslint-disable-next-line
  }, [isError, token]);

  useEffect(() => {
    i18n.changeLanguage(languageStorage);
    // eslint-disable-next-line
  }, [languageStorage]);

  return (
    <ContextProviderLayout>
      <ErrorBoundary>
        <ToastContainer className='foo' style={{ top: '70px' }} />
        <div className='app'>
          <audio ref={audioPlayer} src={NotificationSound} type='audio/mpeg' />
          <HashRouter>
            <AppRouters />
          </HashRouter>
        </div>
      </ErrorBoundary>
    </ContextProviderLayout>
  );
};

export default App;

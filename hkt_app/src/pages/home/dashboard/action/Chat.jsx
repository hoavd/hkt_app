import { Input } from 'antd';
import { useEffect, useRef, useState } from 'react';
import { useSelector } from 'react-redux';
import IconUser from '../../../../assets/images/icon-user.png';
import IconSend from '../../../../assets/images/send.png';
import useNotify from '../../../../hooks/useNotify';
import { getToken, getUsername } from '../../../../utils/storage';
import { chatAiSystem, getChatAiSystem } from '../../../../services/dashboard/alert';
import SockJS from 'sockjs-client';
import { BASE_URL_SOCKET, topicPath } from '../../../../config/apiPath';
import Stomp from 'stompjs';

const { TextArea } = Input;
let stompClient;
const Chat = ({ uuid }) => {
  const [isError, setIsError] = useState(true);
  const token = getToken();
  const notify = useNotify();
  const username = getUsername();
  const [dataAlertMsg, setDataAlertMsg] = useState([]);
  const { userInfoPublic } = useSelector((state) => state.userReducer);

  const boxMessageRef = useRef();
  const [dataMsgMap, setDataMsgMap] = useState([]);
  const [valueInput, setValueInput] = useState(null);
  const [isDisable, setIsDisable] = useState(false);
  const [pageNum, setPageNum] = useState(1);
  // const [isFirst, setIsFirst] = useState(true);

  useEffect(() => {
    if (isError && token) {
      connectServerGetData();
    }
    // eslint-disable-next-line
  }, [isError, token]);
  const connectServerGetData = () => {
    setIsError(false);
    const socket = new SockJS(BASE_URL_SOCKET);
    stompClient = Stomp.over(socket);
    stompClient.debug = function (str) {
      // append the debug log to a #debug div
      console.log(str);
    };
    stompClient.heartbeat = {
      outgoing: 0,
      incoming: 10000
    };
    stompClient.connect({ Authorization: 'Bearer ' + token }, onConnected, onError);
  };
  const onConnected = () => {
    console.log('uuid: ' + uuid);
    if (uuid) {
      stompClient.subscribe(`${topicPath.topicMessage}-${uuid}`, onMessageReceived);
    }
  };

  const onMessageReceived = (payload) => {
    const { data, type } = JSON.parse(payload.body);
    console.log(data);
    setDataAlertMsg(data);
    console.log(type);
  };

  useEffect(() => {
    setDataMsgMap((prev) => {
      if (Array.isArray(dataAlertMsg)) return [...dataAlertMsg];
      if (dataAlertMsg) return [dataAlertMsg, ...prev];
      return prev;
    });
    // eslint-disable-next-line
  }, [dataAlertMsg]);

  const onError = (error) => {
    console.log(error);
    setTimeout(() => {
      setIsError(true);
    }, 5000);
  };

  const handleChangeInput = (e) => {
    const value = e.target.value.trimStart();
    setValueInput(value);
  };

  const handleScroll = (event) => {
    const element = event.target;
    if (element.scrollTop * -1 + element.clientHeight === element.scrollHeight) {
      setPageNum((prevPage) => prevPage + 1);
    }
  };

  const handleSendMsg = (msgInput) => {
    if (!isDisable) {
      setIsDisable(true);
      chatAiSystem(
        { uuid: uuid, msg: msgInput },
        (res) => {
          if (res.data.success) {
            boxMessageRef.current?.scrollTo(0, 0);
          }
          setValueInput(null);
          setIsDisable(false);
        },
        (err) => {
          notify.error(err?.data?.msg);
          setValueInput(null);
          setIsDisable(false);
        }
      );
    }
  };

  const handleOnKeyDown = (e) => {
    if (e.key === 'Enter' && valueInput !== null) {
      handleSendMsg(valueInput);
    }
  };

  useEffect(() => {
    if (uuid)
      getChatAiSystem(
        { max: pageNum * 10, offset: 0, order: 'desc', uuid: uuid },
        (res) => {
          setDataMsgMap(res.data.list);
          console.log(res);
          // dispatch(setDataTranMsg({ data: res.data.list, type: 'api' }));
        },
        (err) => {
          notify.error(err?.data?.msg);
        }
      );
    // eslint-disable-next-line
  }, [pageNum]);

  /*useEffect(() => {
    setDataMsgMap([...dataTranMsg]);
    // eslint-disable-next-line
  }, [dataTranMsg]);*/

  return (
    <div className='action-container'>
      <div className='box-map-msg' ref={boxMessageRef} onScroll={(e) => handleScroll(e)}>
        {dataMsgMap.map((ele, i) => {
          return (
            <div className={userInfoPublic.username === ele.createdBy ? 'messages-send' : 'messages'} key={i}>
              <div className='avatar'>
                <img src={IconUser} alt='' />
              </div>
              <div className='text_wrapper'>
                {ele.createdBy !== username ? <span className='time'>{ele.createdBy}</span> : null}
                <div className='text'>{ele.msg}</div>
                <span className='time'>{ele.createDate}</span>
              </div>
            </div>
          );
        })}
      </div>
      <div className='box-input-msg'>
        <TextArea
          style={{ resize: 'none' }}
          value={valueInput}
          placeholder='Enter ...'
          onChange={handleChangeInput}
          onKeyDown={(e) => {
            handleOnKeyDown(e);
          }}
        />
        <img
          src={IconSend}
          alt=''
          onClick={() => {
            handleSendMsg(valueInput);
          }}
        />
      </div>
    </div>
  );
};

export default Chat;

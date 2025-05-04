import { Input } from 'antd';
import { useEffect, useRef, useState } from 'react';
import { useSelector } from 'react-redux';
import IconUser from '../../../../assets/images/icon-user.png';
import IconSend from '../../../../assets/images/send.png';
import useNotify from '../../../../hooks/useNotify';
import { getUsername } from '../../../../utils/storage';
import { getAllUrlParams } from '../../../../utils/tools';
import { chatAiSystem, getChatAiSystem } from '../../../../services/dashboard/alert';

const { TextArea } = Input;
const Chat = ({ data }) => {
  // const dispatch = useDispatch();
  const notify = useNotify();
  const username = getUsername();
  const { typepic } = getAllUrlParams(window.location.href);
  // const { dataTranMsg } = useSelector((state) => state.tranReducer);
  const { userInfoPublic } = useSelector((state) => state.userReducer);

  const boxMessageRef = useRef();

  const [dataMsgMap, setDataMsgMap] = useState([]);
  const [valueInput, setValueInput] = useState(null);
  const [isDisable, setIsDisable] = useState(false);
  const [pageNum, setPageNum] = useState(1);
  // const [isFirst, setIsFirst] = useState(true);

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
        { alertId: data.id, uuid: data.uuid, msg: msgInput },
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
    if (data?.transactionId)
      getChatAiSystem(
        { max: pageNum * 10, offset: 0, order: 'desc', transactionId: data.transactionId, uuid: data.uuid },
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
          disabled={typepic === 'view'}
          onKeyDown={(e) => {
            if (typepic !== 'view') handleOnKeyDown(e);
          }}
        />
        <img
          src={IconSend}
          alt=''
          onClick={() => {
            if (typepic !== 'view') handleSendMsg(valueInput);
          }}
        />
      </div>
    </div>
  );
};

export default Chat;

import { Tabs } from 'antd';
import { useState } from 'react';
import Chat from './Chat';
import './style.scss';
const TabAction = ({ showTab }) => {
  // const dispatch = useDispatch();
  // const { isViewChat, dataTranMsgQue } = useSelector((state) => state.tranReducer);
  // const [isViewChatState, setIsViewChatState] = useState(false);
  // const [isDataChat, setIsDataChat] = useState(false);
  const [activeKey, setActiveKey] = useState(1);

  const item = [
    {
      key: 1,
      label: (
        <div
          className='label-tab'
          onClick={() => {
            setActiveKey(1);
          }}
        >
          <span className='label'>Chat</span>
          <div className='dot' />
        </div>
      ),
      children: <Chat data={''} />
    }
  ];

  /*useEffect(() => {
    if (dataTranMsgQue) {
      setActiveKey(2);
      setIsDataChat(true);
    } else {
      setIsDataChat(false);
    }
  }, [dataTranMsgQue]);*/

  return <Tabs activeKey={showTab ? activeKey : 1} items={item} />;
};
export default TabAction;

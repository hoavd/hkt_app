import { Button, Form, Input, Tabs } from 'antd';
import TabAction from './action/TabAction';
import IconCancel from '../../../assets/images/close.png';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import './style.scss';
const Alert = () => {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const itemDestop = [
    {
      key: 1,
      label: 'Thông tin lỗi',
      disabled: true,
      children: (
        <Form form={form} size='small' layout='vertical' disabled={false}>
          <div className='box-insert'>
            <p className='title-box'></p>
            <div className='box-insert-content template-two-col'>
              <Form.Item name={'Test'} label={'Test'} key={1}>
                <Input disabled={true}></Input>
              </Form.Item>
              <Form.Item name={'Test2'} label={'Test 2'} key={2}>
                <Input disabled={true}></Input>
              </Form.Item>
            </div>
          </div>
        </Form>
      )
    }
  ];
  // const { t } = useTranslation();
  return (
    <div className='body-tran-container padding-content-page-tran'>
      <div className='container-tran-content'>
        <Tabs activeKey={1} items={itemDestop} />
        <div className='row-form-button-tran'>
          <Button
            className='btn-add'
            icon={<img src={IconCancel} alt='' />}
            onClick={() => navigate(`/dashboard/listWarning`)}
          >
            {t('close')}
          </Button>
        </div>
      </div>
      <div className='container-tran-action'>
        <Form form={form} size='small' layout='vertical'>
          <TabAction showTab={true} />
        </Form>
      </div>
    </div>
  );
};

export default Alert;

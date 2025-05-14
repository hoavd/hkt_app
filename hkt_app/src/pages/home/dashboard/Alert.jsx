import { Button, Form, Input, Tabs } from 'antd';
import TabAction from './action/TabAction';
import IconCancel from '../../../assets/images/close.png';
import { useTranslation } from 'react-i18next';
import { useNavigate, useParams } from 'react-router-dom';
import './style.scss';
import { useEffect, useState } from 'react';
import { findSolution, getAlert } from '../../../services/dashboard/alert';
import useNotify from '../../../hooks/useNotify';

const Alert = () => {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const notify = useNotify();
  const { id } = useParams();
  const [form] = Form.useForm();
  const [solution, setSolution] = useState('');
  useEffect(() => {
    if (id) {
      getAlert(
        id,
        (res) => {
          const { code, type, severity, impactDetail, rootcause, solution, createDate, desc } = res.data;
          setSolution(solution);
          form.setFieldsValue({
            code: code,
            type: type,
            severity: severity,
            impactDetail: impactDetail,
            rootcause: rootcause,
            solution: solution,
            createDate: createDate,
            desc: desc
          });
        },
        (err) => {
          notify.error(err?.data?.msg);
        }
      );
      if (!solution) {
        findSolution(
          id,
          (res) => {
            console.log(res.data);
          },
          (err) => {
            notify.error(err?.data?.msg);
          }
        );
      }
    }
    // eslint-disable-next-line
  }, [id]);

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
              <Form.Item name={'code'} label={'Code'} key={1}>
                <Input disabled={true}></Input>
              </Form.Item>
              <Form.Item name={'type'} label={'Type'} key={2}>
                <Input disabled={true}></Input>
              </Form.Item>
              <Form.Item name={'severity'} label={'Severity'} key={3}>
                <Input disabled={true}></Input>
              </Form.Item>
              <Form.Item name={'desc'} label={'Desc'} key={4}>
                <Input disabled={true}></Input>
              </Form.Item>
              <Form.Item name={'impactDetail'} label={'Impact Detail'} key={5}>
                <Input disabled={true}></Input>
              </Form.Item>
              <Form.Item name={'rootcause'} label={'Rootcause'} key={6}>
                <Input disabled={true}></Input>
              </Form.Item>
              <Form.Item name={'solution'} label={'Solution'} key={7}>
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
          <TabAction showTab={true} uuid={id} />
        </Form>
      </div>
    </div>
  );
};

export default Alert;

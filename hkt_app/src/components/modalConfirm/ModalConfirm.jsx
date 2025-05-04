import { Modal } from 'antd';
import React from 'react';
import { useTranslation } from 'react-i18next';
import './modalConfirm.scss';

const ModalConfirm = ({ data, isOpen, title, message, handleCancel, handleConfirm, nameShow }) => {
  const { t } = useTranslation();
  return (
    <React.Fragment>
      <Modal
        className='modal-confirm'
        title={title}
        centered
        open={isOpen}
        onOk={handleConfirm}
        onCancel={handleCancel}
        cancelText={t('cancel')}
        okText={t('confirm')}
      >
        <div className='message-modal'>
          <span>{`${message} ${nameShow ? data[nameShow] : ''} !`}</span>
        </div>
      </Modal>
    </React.Fragment>
  );
};

export default ModalConfirm;

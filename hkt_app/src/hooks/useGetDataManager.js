import { useState } from 'react';
import useNotify from './useNotify';
import { getListBranch } from '../services/system/authorService';

const useGetDataManager = () => {
  const notify = useNotify();
  const [listBranch, setBranchList] = useState([]);

  const getBranch = (params) => {
    getListBranch(
      params,
      (res) => {
        let branchOptions = res.data.list.map((item) => ({
          label: item.name,
          value: item.id
        }));
        setBranchList(branchOptions);
      },
      (err) => {
        notify.error(err?.data?.msg);
      }
    );
  };

  return {
    // cif
    listBranch,
    getBranch
  };
};

export default useGetDataManager;

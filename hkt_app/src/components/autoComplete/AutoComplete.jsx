import { AutoComplete } from 'antd';
import React, { useEffect, useState } from 'react';
import IconArrow from '../../assets/images/select-down.png';
import { getCompleteRequest } from '../../services/completeService';

const AutoCompleteCustom = ({
  record,
  index,
  url,
  hanldeChangeField,
  value,
  nameList,
  nameTotal,
  size,
  elementId,
  display
}) => {
  const [valueInput, setValueInput] = useState(null);
  const [valueSelect, setValueSelect] = useState(null);
  const [suggestion, setSuggestions] = useState([]);
  const [totalRecord, setTotalRecord] = useState(0);
  const [pageNum, setPageNum] = useState(0);

  const getSuggestions = (params, check) => {
    getCompleteRequest(`${url}`, params, (res) => {
      setTotalRecord(res.data[nameTotal]);
      let options = [];
      if (elementId === 'topbar') {
        options = res.data[nameList].map((item) => ({
          label: `${item.code} - ${item.name}`,
          value: `${item.code} - ${item.name}`,
          item: item
        }));
      } else if (display === 'both') {
        options = res.data[nameList].map((item) => ({
          label: `${item.code} - ${item.name}`,
          value: `${item.code} - ${item.name}`,
          item: item
        }));
      } else if (display === 'name') {
        options = res.data[nameList].map((item) => ({
          label: `${item.name}`,
          value: `${item.name}`,
          item: item
        }));
      } else {
        options = res.data[nameList].map((item) => ({
          label: `${item.code}`,
          value: `${item.code}`,
          item: item
        }));
      }
      if (check) setSuggestions((oldState) => oldState.concat(options));
      else setSuggestions(options);
    });
  };

  const handleChange = (value, data) => {
    const blurInput = document.getElementById(elementId);
    setValueInput(value);
    hanldeChangeField(data, record, index, value);
    blurInput.blur();
  };

  const handleScroll = (event) => {
    const element = event.target;
    if (element.scrollTop + element.clientHeight === element.scrollHeight) {
      setPageNum((prevPage) => prevPage + 1);
    }
  };

  useEffect(() => {
    setValueSelect(value);
  }, [value]);

  useEffect(() => {
    getSuggestions({ max: 10, offset: 0, order: 'desc' });
    // eslint-disable-next-line
  }, [url]);

  useEffect(() => {
    const offset = pageNum * 10;
    if (pageNum && offset <= totalRecord) {
      getSuggestions({ max: 10, offset: offset, order: 'desc', query: valueInput }, true);
    }
    // eslint-disable-next-line
  }, [pageNum]);

  useEffect(() => {
    if (valueInput || valueInput === '') {
      const delayDebounceFn = setTimeout(() => {
        getSuggestions({ max: 10, offset: 0, order: 'desc', query: valueInput });
      }, 500);
      return () => clearTimeout(delayDebounceFn);
    }
    // eslint-disable-next-line
  }, [valueInput]);

  return (
    <React.Fragment>
      <AutoComplete
        id={elementId}
        options={suggestion}
        defaultValue={valueSelect}
        onSelect={handleChange}
        onSearch={(text) => setValueInput(text)}
        onPopupScroll={handleScroll}
        placeholder={valueSelect}
        value={valueInput}
        onBlur={() => {
          setValueInput('');
          setPageNum(0);
        }}
        size={size}
      />
      <img src={IconArrow} alt='' className='img-select' />
    </React.Fragment>
  );
};

export default AutoCompleteCustom;

import { createSlice } from '@reduxjs/toolkit';

const initialState = {
  listAlert: [],
  alert: {},
  listExchange: [],
  currencyPairItem: {},
  listExchangeCurrenPair: [],
  isShowMenu: false,
  isCollapseMenu: true,
  cifIdSelect: null,
  codeGroupCifSelect: null,
  payType: 'transfer',
  typeCustomer: null,
  listRowSelect: [],
  listRowSelectFull: [],
  valueLang: null,
  timeCloseSys: {},
  listCurrencyAllow: [],
  widthSys: 0
};

export const appSlice = createSlice({
  name: 'appSlice',
  initialState,
  reducers: {
    setListAlert: (state, action) => {
      state.listAlert = action.payload;
    },
    setAlert: (state, action) => {
      state.alert = action.payload;
    },
    setShowMenu: (state, action) => {
      state.isShowMenu = action.payload;
    },
    setCollapseMenu: (state, action) => {
      state.isCollapseMenu = action.payload;
    },
    setTimeCloseSystem: (state, action) => {
      state.timeCloseSys = action.payload;
    },
    // row selection
    setRowSelection: (state, action) => {
      const { selectedRows } = action.payload;
      let arrClone = [];
      if (selectedRows.length) {
        selectedRows.forEach((element) => {
          arrClone.push(element.id);
        });
        state.listRowSelectFull = [...selectedRows];
        state.listRowSelect = [...arrClone];
      } else {
        state.listRowSelectFull = [];
        state.listRowSelect = [...arrClone];
      }
    },
    setRowSelectionAll: (state, action) => {
      const { selected, selectedRows } = action.payload;
      let arrClone = [];
      if (selected) {
        selectedRows.forEach((element) => {
          arrClone.push(element.id);
        });
        state.listRowSelectFull = [...selectedRows];
        state.listRowSelect = [...arrClone];
      } else {
        state.listRowSelectFull = [];
        state.listRowSelect = [...arrClone];
      }
    },
    setValueLang: (state, action) => {
      state.valueLang = action.payload;
    },
    setWidthSystem: (state, action) => {
      state.widthSys = action.payload;
    }
  }
});

export const {
  setListAlert,
  setAlert,
  setShowMenu,
  setCollapseMenu,
  setTimeCloseSystem,
  setTypeCustomer,
  setRowSelection,
  setRowSelectionAll,
  setValueLang,
  setListCurrencyAllow,
  setWidthSystem
} = appSlice.actions;

export default appSlice.reducer;

import { createSlice } from '@reduxjs/toolkit';

const initialState = {
  userInfo: {},
  userInfoPublic: {}
};

export const userInfoSlice = createSlice({
  name: 'userInfo',
  initialState,
  reducers: {
    saveInfoUserLogin: (state, action) => {
      state.userInfo = action.payload;
    },
    savePublicInfoUser: (state, action) => {
      state.userInfoPublic = action.payload;
    }
  }
});

export const { saveInfoUserLogin, savePublicInfoUser } = userInfoSlice.actions;

export default userInfoSlice.reducer;

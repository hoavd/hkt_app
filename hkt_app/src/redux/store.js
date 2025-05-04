import { combineReducers, configureStore } from '@reduxjs/toolkit';
import thunk from 'redux-thunk';
import appReducer from './slice/appSlice';
import userReducer from './slice/userSlice';

const rootReducer = combineReducers({
  appReducer,
  userReducer
});

export const store = configureStore({
  reducer: rootReducer,
  middleware: (getDefaultMiddleware) => getDefaultMiddleware({ serializableCheck: false }).concat(thunk)
});

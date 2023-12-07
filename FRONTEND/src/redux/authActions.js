import {
  AUTH_REQ,
  AUTH_SUCCESS,
  AUTH_FAILURE,
  AUTH_SUCCESS_LOGIN,
  AUTH_FORGOT,
  AUTH_RESET,
} from "./types";

export const authenticate = () => {
  return {
    type: AUTH_REQ,
  };
};

export const authSuccess = (content) => {
  localStorage.setItem("USER_KEY", content.token);
  return {
    type: AUTH_SUCCESS,
    payload: content,
  };
};

export const authSuccessLogin = (content) => {
  localStorage.setItem("LOGIN_KEY", content.token);
  localStorage.setItem("REMEMBER_ME", content.rememberMe);
  return {
    type: AUTH_SUCCESS_LOGIN,
    payload: content,
  };
};

export const authFailure = (error) => {
  return {
    type: AUTH_FAILURE,
    payload: error,
  };
};

export const forgotPassword = (content) => {
  return {
    type: AUTH_FORGOT,
    payload: content,
  };
};

export const resetPassword = (content) => {
  return {
    type: AUTH_RESET,
    payload: content,
  };
};

import {
  AUTH_REQ,
  AUTH_SUCCESS,
  AUTH_FAILURE,
  AUTH_SUCCESS_LOGIN,
  AUTH_FORGOT,
} from "../types";

const initialState = {
  user: {},
  error: "",
  loading: false,
};

const auth = (state = initialState, action) => {
  console.log("Reducer auth");
  switch (action.type) {
    case AUTH_REQ:
      return { ...state, error: "", loading: true };

    case AUTH_SUCCESS:
      const data = action.payload;
      return { ...state, error: "", loading: false, user: data };

    case AUTH_SUCCESS_LOGIN:
      const datalogin = action.payload;
      return { ...state, error: "", loading: false, user: datalogin };

    case AUTH_FAILURE:
      const error = action.payload;
      return { ...state, loading: false, error: error };

    case AUTH_FORGOT:
      const dataForgot = action.payload;
      return { ...state, loading: false, error: "", user: dataForgot };

    default:
      return state;
  }
};

export default auth;

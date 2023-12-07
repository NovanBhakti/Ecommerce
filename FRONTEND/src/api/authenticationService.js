import React from "react";
import axios from "axios";

const getToken = () => {
  return localStorage.getItem("USER_KEY");
};

const getTokenLogin = () => {
  return localStorage.getItem("LOGIN_KEY");
};

const rememberMe = () => {
  return localStorage.getItem("REMEMBER_ME");
};

export const userLogin = (authRequest) => {
  return axios({
    method: "POST",
    url: `${
      process.env.hostUrl || "http://localhost:8080"
    }/api/v1/auth/authenticate`,
    data: authRequest,
  });
};

export const userRegister = (authRequest) => {
  return axios({
    method: "POST",
    url: `${
      process.env.hostUrl || "http://localhost:8080"
    }/api/v1/auth/register`,
    data: authRequest,
  });
};

export const fetchUserData = (authRequest) => {
  return axios({
    method: "GET",
    url: `${
      process.env.hostUrl || "http://localhost:8080"
    }/api/v1/auth/authenticated/home`,
    data: authRequest,
    headers: {
      Authorization: "Bearer " + getTokenLogin(),
    },
  });
};

export const fetchUserDataRememberMe = (authRequest) => {
  return axios({
    method: "GET",
    url: `${process.env.hostUrl || "http://localhost:8080"}/api/v1/auth/home`,
    headers: {
      Authorization: "Bearer " + rememberMe(),
    },
  });
};

export const fetchUserForgotPassword = (authRequest) => {
  return axios({
    method: "POST",
    url: `${
      process.env.hostUrl || "http://localhost:8080"
    }/api/v1/auth/forgot-password`,
    data: authRequest,
  });
};

export const fetchUserResetPassword = (authRequest, token) => {
  return axios({
    method: "POST",
    url: `${
      process.env.hostUrl || "http://localhost:8080"
    }/api/v1/auth/reset-password?token=${token}`,
    data: authRequest,
  });
};

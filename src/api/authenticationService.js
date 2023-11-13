import React from "react";
import axios from "axios";

const getToken = () => {
  return localStorage.getItem("USER_KEY");
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
    }/api/v1/auth//api/v1/demo-controller`,
    headers: {
      Authorization: "Bearer " + getToken(),
    },
  });
};

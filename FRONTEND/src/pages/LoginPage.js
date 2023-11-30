import React, { useState, useEffect } from "react";
import { connect } from "react-redux";
import {
  authenticate,
  authFailure,
  authSuccess,
  authSuccessLogin,
} from "../redux/authActions";
import "./loginpage.css";
import {
  userLogin,
  fetchUserDataRememberMe,
} from "../api/authenticationService";
import { Alert, Spinner } from "react-bootstrap";
import { Link } from "react-router-dom/cjs/react-router-dom.min";
import Header from "../components/Header";
import Swal from "sweetalert2";

const LoginPage = ({ loading, error, ...props }) => {
  const [values, setValues] = useState({
    email: "",
    password: "",
  });

  const [rememberMe, setRememberMe] = useState(false);
  const [data, setData] = useState({});
  let dataFetched = false;

  const handleSubmit = (evt) => {
    evt.preventDefault();
    props.authenticate();

    userLogin(values)
      .then((response) => {
        console.log("response", response);
        if (response.status === 200) {
          props.setUser(response.data);
          if (rememberMe === true) {
            localStorage.setItem("REMEMBER_ME", response.data.token);
          } else {
            localStorage.removeItem("REMEMBER_ME");
          }
          if (localStorage.getItem("USER_KEY")) {
            localStorage.removeItem("USER_KEY");
          }
          props.history.push("/dashboard");
        } else {
          Swal.fire({
            position: "top-center",
            icon: "error",
            title: `${response.data.message}`,
            showConfirmButton: false,
            timer: 1500,
          });
        }
      })
      .catch((err) => {
        if (err && err.response) {
          switch (err.response.status) {
            case 401:
              console.log("401 status");
              Swal.fire({
                icon: "error",
                title: `${err.response.data.message}`,
                showConfirmButton: false,
                timer: 1500,
              });
              break;
            case 403:
              console.log("403 status");
              Swal.fire({
                icon: "error",
                title: `${err.response.data.message}`,
                showConfirmButton: false,
                timer: 1500,
              });
              break;
            default:
              Swal.fire({
                icon: "error",
                title: `${err.response.data.message}`,
                showConfirmButton: false,
                timer: 1500,
              });
              break;
          }
        } else {
          Swal.fire({
            icon: "error",
            title: "SOMTHING WENT WRONG",
            showConfirmButton: false,
            timer: 1500,
          });
        }
      });

    //console.log("Loading again",loading);
  };

  const handleChange = (e) => {
    e.persist();
    setValues((values) => ({
      ...values,
      [e.target.name]: e.target.value,
    }));
  };

  const handleRememberMeChange = () => {
    setRememberMe(!rememberMe);
  };

  // if (localStorage.getItem("REMEMBER_ME") && !dataFetched) {
  //   fetchUserDataRememberMe()
  //     .then((response) => {
  //       setData(response.data);
  //     })
  //     .catch((e) => {
  //       console.error("Error fetching user data:");
  //     });
  // }

  return (
    <div>
      <Header />
      <div className="login-page">
        <section className="h-100">
          <div className="container h-100">
            <div className="row justify-content-md-center h-100">
              <div className="card-wrapper">
                <div className="card fat">
                  <div className="card-body">
                    <h4 className="card-title">Login</h4>

                    <form
                      className="my-login-validation"
                      onSubmit={handleSubmit}
                      noValidate={false}
                    >
                      {localStorage.getItem("REMEMBER_ME") ? (
                        <>
                          <div className="form-group">
                            <label htmlFor="email">email</label>
                            <input
                              id="email"
                              type="text"
                              className="form-control"
                              minLength={5}
                              value={data && `${data.email}`}
                              onChange={handleChange}
                              name="email"
                              required
                            />

                            <div className="invalid-feedback">
                              UserId is invalid
                            </div>
                          </div>

                          <div className="form-group">
                            <label>
                              Password
                              <Link
                                to="/forgot-password"
                                className="float-right"
                              >
                                Forgot Password?
                              </Link>
                            </label>
                            <input
                              id="password"
                              type="password"
                              className="form-control"
                              minLength={8}
                              value={data && `${data.password}`}
                              onChange={handleChange}
                              name="password"
                              required
                            />
                            <div className="invalid-feedback">
                              Password is required
                            </div>
                          </div>
                        </>
                      ) : (
                        <>
                          <div className="form-group">
                            <label htmlFor="email">email</label>
                            <input
                              id="email"
                              type="text"
                              className="form-control"
                              minLength={5}
                              value={values.email}
                              onChange={handleChange}
                              name="email"
                              required
                            />

                            <div className="invalid-feedback">
                              UserId is invalid
                            </div>
                          </div>

                          <div className="form-group">
                            <label>
                              Password
                              <Link
                                to="/forgot-password"
                                className="float-right"
                              >
                                Forgot Password?
                              </Link>
                            </label>
                            <input
                              id="password"
                              type="password"
                              className="form-control"
                              minLength={8}
                              value={values.password}
                              onChange={handleChange}
                              name="password"
                              required
                            />
                            <div className="invalid-feedback">
                              Password is required
                            </div>
                          </div>
                        </>
                      )}

                      <div className="form-group">
                        <div className="custom-control custom-checkbox">
                          <input
                            type="checkbox"
                            className="custom-control-input"
                            id="customCheck1"
                            checked={rememberMe}
                            onChange={handleRememberMeChange}
                          />
                          <label
                            className="custom-control-label"
                            htmlFor="customCheck1"
                          >
                            Remember me
                          </label>
                        </div>
                      </div>

                      <div className="form-group m-0 text-center mb-2">
                        <button
                          type="submit"
                          className="btn btn-primary rounded-pill"
                          style={{ width: "100%" }}
                        >
                          Login
                          {loading && (
                            <Spinner
                              as="span"
                              animation="border"
                              size="sm"
                              role="status"
                              aria-hidden="true"
                            />
                          )}
                          {/* <ClipLoader
                                        //css={override}
                                        size={20}
                                        color={"#123abc"}
                                        loading={loading}
                                        /> */}
                        </button>
                      </div>
                    </form>
                    <div className="text-center">
                      Don't have Account? <Link to="/register">Register</Link>
                    </div>
                    {error && (
                      <Alert style={{ marginTop: "20px" }} variant="danger">
                        {error}
                      </Alert>
                    )}
                  </div>
                </div>
              </div>
            </div>
          </div>
        </section>
      </div>
    </div>
  );
};

const mapStateToProps = ({ auth }) => {
  console.log("state ", auth);
  return {
    loading: auth.loading,
    error: auth.error,
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    authenticate: () => dispatch(authenticate()),
    setUser: (data) => dispatch(authSuccessLogin(data)),
    loginFailure: (message) => dispatch(authFailure(message)),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(LoginPage);

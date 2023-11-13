import react, { useState } from "react";
import { connect } from "react-redux";
import { authenticate, authFailure, authSuccess } from "../redux/authActions";
import "./loginpage.css";
import { userRegister } from "../api/authenticationService";
import { Alert, Spinner } from "react-bootstrap";
import { Link } from "react-router-dom/cjs/react-router-dom.min";

const RegisterPage = ({ loading, error, ...props }) => {
  const [values, setValues] = useState({
    firstName: "",
    lastName: "",
    email: "",
    password: "",
  });

  const handleSubmit = (evt) => {
    evt.preventDefault();

    userRegister(values)
      .then((response) => {
        console.log("response", response);
        if (response.status === 200) {
          props.setUser(response.data);
          props.history.push("/");
        } else {
          props.loginFailure("Something Wrong!Please Try Again");
        }
      })
      .catch((err) => {
        if (err && err.response) {
          switch (err.response.status) {
            case 401:
              console.log("401 status");
              props.registerFailure("Authentication Failed.Bad Credentials");
              break;
            default:
              props.registerFailure("Something Wrong!Please Try Again");
          }
        } else {
          props.registerFailure("Something Wrong!Please Try Again");
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

  console.log("Loading ", loading);

  return (
    <div className="login-page">
      <section className="h-100">
        <div className="container h-100">
          <div className="row justify-content-md-center h-100">
            <div className="card-wrapper">
              <div className="card fat">
                <div className="card-body">
                  <h4 className="card-title">Register</h4>

                  <form
                    className="my-login-validation"
                    onSubmit={handleSubmit}
                    noValidate={false}
                  >
                    <div className="form-group">
                      <label htmlFor="email">First Name</label>
                      <input
                        id="firstname"
                        type="text"
                        className="form-control"
                        minLength={5}
                        value={values.firstName}
                        onChange={handleChange}
                        name="firstName"
                        required
                      />

                      <div className="invalid-feedback">UserId is invalid</div>
                    </div>

                    <div className="form-group">
                      <label htmlFor="email">Last Name</label>
                      <input
                        id="lastname"
                        type="text"
                        className="form-control"
                        minLength={5}
                        value={values.lastName}
                        onChange={handleChange}
                        name="lastName"
                        required
                      />

                      <div className="invalid-feedback">UserId is invalid</div>
                    </div>

                    <div className="form-group">
                      <label htmlFor="email">Email</label>
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

                      <div className="invalid-feedback">UserId is invalid</div>
                    </div>

                    <div className="form-group">
                      <label>
                        Password
                        <a href="forgot.html" className="float-right">
                          Forgot Password?
                        </a>
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

                    <div className="form-group">
                      <div className="custom-control custom-checkbox">
                        <input
                          type="checkbox"
                          className="custom-control-input"
                          id="customCheck1"
                        />
                        <label
                          className="custom-control-label"
                          htmlFor="customCheck1"
                        >
                          Remember me
                        </label>
                      </div>
                    </div>

                    <div className="form-group m-0">
                      <button type="submit" className="btn btn-primary">
                        Register
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
    setUser: (data) => dispatch(authSuccess(data)),
    registerFailure: (message) => dispatch(authFailure(message)),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(RegisterPage);

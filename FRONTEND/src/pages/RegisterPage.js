import { useState } from "react";
import { connect } from "react-redux";
import { authFailure, authSuccess } from "../redux/authActions";
import "./loginpage.css";
import { userRegister } from "../api/authenticationService";
import { Alert, Spinner } from "react-bootstrap";
import { Link } from "react-router-dom/cjs/react-router-dom.min";
// import Navbar from "../components/Navbar";
import Header from "../components/Header";
import Swal from "sweetalert2";
// import withReactContent from "sweetalert2-react-content";

// const MySwal = withReactContent(Swal);
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
        if (response.status === 200) {
          props.setUser(response.data.data);
          Swal.fire({
            position: "top-center",
            icon: "success",
            title: `${response.data.message}`,
            showConfirmButton: false,
            timer: 1500,
          });
          props.history.push("/login");
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
            default:
              Swal.fire({
                icon: "error",
                title: `${err.response.data.message}`,
                showConfirmButton: false,
                timer: 1500,
              });
              break;
            // props.registerFailure("Something Wrong!Please Try Again");
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
  };

  const handleChange = (e) => {
    e.persist();
    setValues((values) => ({
      ...values,
      [e.target.name]: e.target.value,
    }));
  };

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

                        <div className="invalid-feedback">
                          UserId is invalid
                        </div>
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

                        <div className="invalid-feedback">
                          UserId is invalid
                        </div>
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

                        <div className="invalid-feedback">
                          UserId is invalid
                        </div>
                      </div>

                      <div className="form-group">
                        <label>Password</label>
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

                      <div className="form-group mt-2 text-center">
                        <button
                          type="submit"
                          className="btn btn-primary rounded-pill"
                          style={{ width: "100%" }}
                        >
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
                    <div className="text-center mb-2">
                      have Account? <Link to="/login">Login</Link>
                    </div>
                    {props.registerFailure ? (
                      <>
                        {error && (
                          <Alert style={{ marginTop: "20px" }} variant="danger">
                            {error}
                          </Alert>
                        )}
                      </>
                    ) : (
                      <>
                        {user && (
                          <Alert
                            style={{ marginTop: "20px" }}
                            variant="success"
                          >
                            {user}
                          </Alert>
                        )}
                      </>
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
  return {
    loading: auth.loading,
    error: auth.error,
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    setUser: (data) => dispatch(authSuccess(data)),
    registerSuccess: (message) => dispatch(authSuccess(message)),
    registerFailure: (message) => dispatch(authFailure(message)),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(RegisterPage);

import react, { useState } from "react";
import { connect } from "react-redux";
import { resetPassword } from "../redux/authActions";
import "./loginpage.css";
import { fetchUserResetPassword } from "../api/authenticationService";
import { Spinner } from "react-bootstrap";
import Swal from "sweetalert2";

const ResetPassword = ({ loading, error, ...props }) => {
  const [values, setValues] = useState({
    newPassword: "",
    confirmPassword: "",
  });

  const urlParams = new URLSearchParams(window.location.search);
  const token = urlParams.get("token");

  const handleSubmit = (evt) => {
    evt.preventDefault();

    fetchUserResetPassword(values, token)
      .then((response) => {
        if (response.status === 200) {
          props.setUser(response.data);
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
      <div className="login-page">
        <section className="h-100">
          <div className="container h-100">
            <div className="row justify-content-md-center h-100">
              <div className="card-wrapper">
                <div className="card fat">
                  <div className="card-body">
                    <h4 className="card-title">Reset Password</h4>

                    <form
                      className="my-login-validation"
                      onSubmit={handleSubmit}
                      noValidate={false}
                    >
                      <div className="form-group">
                        <label htmlFor="newPassword">New Password</label>
                        <input
                          id="newPassword"
                          type="password"
                          className="form-control"
                          minLength={5}
                          value={values.newPassword}
                          onChange={handleChange}
                          name="newPassword"
                          required
                        />
                      </div>

                      <div className="form-group">
                        <label htmlFor="confirmPassword">
                          Confirm Password
                        </label>
                        <input
                          id="confirmPassword"
                          type="password"
                          className="form-control"
                          minLength={5}
                          value={values.confirmPassword}
                          onChange={handleChange}
                          name="confirmPassword"
                          required
                        />
                      </div>
                      <div className="form-group m-0 text-center">
                        <button
                          type="submit"
                          className="btn btn-primary rounded-pill"
                          style={{ width: "100%" }}
                        >
                          Create New Password
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
    setUser: (data) => dispatch(resetPassword(data)),
    resetSuccess: (message) => dispatch(resetPassword(message)),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(ResetPassword);

import { useState } from "react";
import { connect } from "react-redux";
import { forgotPassword } from "../redux/authActions";
import "./loginpage.css";
import { fetchUserForgotPassword } from "../api/authenticationService";
import Header from "../components/Header";
import Swal from "sweetalert2";

const ForgotPassword = ({ loading, error, ...props }) => {
  const [values, setValues] = useState({
    email: "",
  });
  const [data, setData] = useState({});

  const handleSubmit = (evt) => {
    evt.preventDefault();

    fetchUserForgotPassword(values)
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
          props.history.push("/resetdesc");
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
      <Header />
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
                        <label htmlFor="email">Input Your Email</label>
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
                      <div className="form-group m-0 text-center">
                        <button
                          type="submit"
                          className="btn btn-primary rounded-pill"
                          style={{ width: "100%" }}
                        >
                          Reset Password
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
    setUser: (data) => dispatch(forgotPassword(data)),
    forgotSuccess: (message) => dispatch(forgotPassword(message)),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(ForgotPassword);

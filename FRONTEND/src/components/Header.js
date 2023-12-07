import Container from "react-bootstrap/Container";
import Nav from "react-bootstrap/Nav";
import Navbar from "react-bootstrap/Navbar";
import {
  withRouter,
  NavLink,
  Link,
} from "react-router-dom/cjs/react-router-dom.min";
import "../components/navbar.css";
import React, { useState } from "react";
import Swal from "sweetalert2";
import { fetchUserData } from "../api/authenticationService";

const Header = (props) => {
  const user = localStorage.getItem("LOGIN_KEY");
  const [expanded, setExpanded] = useState(false);
  const [isDropdownOpen, setDropdownOpen] = useState(false);
  const [data, setData] = useState({});
  const handleAvatarClick = () => {
    setDropdownOpen(!isDropdownOpen);
  };

  React.useEffect(() => {
    let isMounted = true;
    if (user != null) {
      fetchUserData()
        .then((response) => {
          if (isMounted) {
            setData(response.data.data);
          }
        })
        .catch((e) => {
          console.log("error");
        });
    }
    return () => {
      isMounted = false;
    };
  }, [user]);

  const logOut = () => {
    Swal.fire({
      title: "Are you sure want to exit?",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#d33",
      confirmButtonText: "Confirm",
    }).then((result) => {
      if (result.isConfirmed) {
        localStorage.removeItem("LOGIN_KEY");
        props.history.push("/login");
      }
      () => {
        isMounted = false;
      };
    });
  };

  return (
    <>
      <Navbar className="navbar shadow-sm sticky-top" expand="sm">
        <Container>
          <Navbar.Brand href="/dashboard">
            <img src="./Gorgosaurus_BW_transparent.png" width={50} alt="Logo" />
          </Navbar.Brand>
          <Navbar.Toggle
            aria-controls="basic-navbar-nav"
            onClick={() => setExpanded(!expanded)}
          />
          <Navbar.Collapse
            id="basic-navbar-nav"
            className={expanded ? "show text-end" : "text-end"}
          >
            <Nav className="mx-auto">
              {user ? (
                <>
                  <NavLink
                    activeClassName="navbar__link--active"
                    className="navbar__link animated fade-in"
                    to="/dashboard"
                  >
                    Home
                  </NavLink>
                  <NavLink
                    activeClassName="navbar__link--active"
                    className="navbar__link animated fade-in"
                    to="/test"
                  >
                    Test
                  </NavLink>
                </>
              ) : null}
            </Nav>
            <Nav>
              {user ? (
                <>
                  <div>
                    <img
                      src="https://mdbcdn.b-cdn.net/img/new/avatars/2.webp"
                      className="rounded-circle"
                      style={{ width: "50px", cursor: "pointer" }}
                      alt="Avatar"
                      onClick={handleAvatarClick}
                    />

                    {isDropdownOpen && (
                      <div style={{ position: "relative" }}>
                        <div
                          style={{
                            position: "fixed",
                            top: 0,
                            right: 0,
                            bottom: 0,
                            left: 0,
                          }}
                          onClick={() => setDropdownOpen(false)}
                        />
                        <div
                          style={{
                            position: "absolute",
                            top: "100%",
                            right: 0,
                            background: "#fff",
                            border: "1px solid #ccc",
                            boxShadow: "0 4px 8px rgba(0, 0, 0, 0.1)",
                            zIndex: 1000,
                            borderRadius: "5px",
                            marginTop: "10px",
                            padding: "10px",
                          }}
                        >
                          {/* Isi dropdown di sini */}
                          <div className="text-center">
                            <div className="">
                              <div>{data && `${data.firstName}`}</div>
                              <Link to="/update-profile">Update Profile</Link>
                            </div>

                            <div>
                              <button
                                className="btn btn-outline-danger rounded-pill pb-2"
                                type="button"
                                onClick={() => logOut()}
                              >
                                Logout
                              </button>
                            </div>
                          </div>
                        </div>
                      </div>
                    )}
                  </div>
                </>
              ) : (
                <>
                  <Nav className="">
                    <NavLink
                      activeClassName="navbar__link--active"
                      className="navbar__link animated fade-in"
                      to="/login"
                    >
                      Login
                    </NavLink>
                    <NavLink
                      activeClassName="navbar__link--active"
                      className="navbar__link animated fade-in"
                      to="/register"
                    >
                      Register
                    </NavLink>
                  </Nav>
                </>
              )}
            </Nav>
          </Navbar.Collapse>
        </Container>
      </Navbar>
    </>
  );
};

export default withRouter(Header);

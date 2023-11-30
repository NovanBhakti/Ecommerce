import Container from "react-bootstrap/Container";
import Nav from "react-bootstrap/Nav";
import Navbar from "react-bootstrap/Navbar";
import {
  Link,
  withRouter,
  NavLink,
  useLocation,
} from "react-router-dom/cjs/react-router-dom.min";
import "../components/navbar.css";
import { Button } from "react-bootstrap";
import { useState } from "react";
import Swal from "sweetalert2";

const Header = (props) => {
  const user = localStorage.getItem("LOGIN_KEY");
  const [expanded, setExpanded] = useState(false);
  const location = useLocation();

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
            className={expanded ? "show text-center" : "text-center"}
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
                  <button
                    className="btn btn-outline-danger rounded-pill pb-2"
                    type="button"
                    onClick={() => logOut()}
                  >
                    Logout
                  </button>
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

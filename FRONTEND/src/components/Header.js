import Container from "react-bootstrap/Container";
import Nav from "react-bootstrap/Nav";
import Navbar from "react-bootstrap/Navbar";
import { Link, withRouter } from "react-router-dom/cjs/react-router-dom.min";
import "./navbar.css";
import { Button } from "react-bootstrap";

const Header = (props) => {
  const user = localStorage.getItem("USER_KEY");

  const logOut = () => {
    localStorage.clear();
    props.history.push("/");
  };

  return (
    <>
      <Navbar className="navbar">
        <Container>
          <Navbar.Brand href="/">Navbar</Navbar.Brand>
          <Nav>
            {user ? (
              <>
                <Link className="nav-link" to="/dashboard">
                  Home
                </Link>
                <Button
                  className="btn btn-primary rounded-pill "
                  onClick={() => logOut()}
                >
                  Log Out
                </Button>
              </>
            ) : (
              <>
                <Nav className="me-auto">
                  <Link className="nav-link" to="/">
                    Login
                  </Link>
                  <Link className="nav-link" to="/register">
                    Register
                  </Link>
                </Nav>
              </>
            )}
          </Nav>
        </Container>
      </Navbar>
    </>
  );
};

export default withRouter(Header);

import Container from "react-bootstrap/Container";
import Nav from "react-bootstrap/Nav";
import Navbar from "react-bootstrap/Navbar";
import { Link } from "react-router-dom/cjs/react-router-dom.min";
import "./navbar.css";
import { Button } from "react-bootstrap";

function navbar() {
  const user = localStorage.getItem("USER_KEY");

  const logOut = () => {
    localStorage.clear();
    history.push("/");
  };
  return (
    <>
      <Navbar className="navbar">
        <Container>
          <Navbar.Brand href="#home">Navbar</Navbar.Brand>
          <Nav>
            {user ? (
              <>
                <NaLink className="nav-link" to="/dashboard">
                  Home
                </NaLink>
                <Button className="rounded" onClick={() => logOut()}>
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
}

export default navbar;

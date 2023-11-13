import Container from "react-bootstrap/Container";
import Nav from "react-bootstrap/Nav";
import Navbar from "react-bootstrap/Navbar";
import { Link } from "react-router-dom/cjs/react-router-dom.min";
import "./navbar.css";

function ColorSchemesExample() {
  return (
    <>
      <Navbar className="navbar">
        <Container>
          <Navbar.Brand href="#home">Navbar</Navbar.Brand>
          <Nav className="me-auto">
            <Link className="nav-link" to="/dashboard">
              Home
            </Link>
            <Link className="nav-link" to="/">
              Login
            </Link>
            <Link className="nav-link" to="/register">
              Register
            </Link>
          </Nav>
        </Container>
      </Navbar>
    </>
  );
}

export default ColorSchemesExample;

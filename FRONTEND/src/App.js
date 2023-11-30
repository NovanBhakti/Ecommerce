import logo from "./logo.svg";
import "./App.css";
import { BrowserRouter, Switch, Route, Redirect } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import { Dashboard } from "./pages/dashboard/dashboard";
import RegisterPage from "./pages/RegisterPage";
import Test from "./pages/dashboard/Test";
import ForgotPassword from "./pages/ForgotPassword";
import ResetDescription from "./pages/ResetDescription";
function App() {
  const user = localStorage.getItem("LOGIN_KEY");
  return (
    <BrowserRouter>
      {user ? (
        <>
          <Redirect from="/" to="/dashboard" />
        </>
      ) : (
        <>
          <Redirect from="/" to="/login" />
        </>
      )}

      <Switch>
        <Route exact path="/login" component={LoginPage} />
        <Route exact path="/register" component={RegisterPage} />
        <Route exact path="/dashboard" component={Dashboard} />
        <Route exact path="/test" component={Test} />
        <Route exact path="/forgot-password" component={ForgotPassword} />
        <Route exact path="/resetdesc" component={ResetDescription} />
      </Switch>
    </BrowserRouter>
  );
}

export default App;

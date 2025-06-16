import LoginGuide from '@/components/LoginGuide';
import { ACCESS_TOKEN } from "@/constant";

export const withAuth = (Component) => () => {
  const loginToken = localStorage.getItem(ACCESS_TOKEN);

  if (loginToken) {
    return <Component />;
  } else {
    return <LoginGuide />;
  }
}
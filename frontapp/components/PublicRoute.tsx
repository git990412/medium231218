import { ComponentType, FC, useEffect } from "react";
import { useSelector } from "react-redux";
import { RootState } from "../store/store"; // Redux store의 RootState를 가져옵니다.
import { useRouter } from "next/navigation";

const PublicRoute = (WrappedComponent: ComponentType) => {
  return (props: any) => {
    // 실제 어플리케이션에서는 로그인 상태를 글로벌 상태, 쿠키,
    // 또는 로컬 스토리지에서 가져오거나 백엔드와의 통신을 통해 결정해야 합니다.
    const isLoggedIn = useSelector((state: RootState) => state.user.isLoggedIn);
    const router = useRouter();

    useEffect(() => {
      if (isLoggedIn) {
        // 로그인 상태일 때 리다이렉션할 경로
        router.replace("/");
      }
    }, [isLoggedIn, router]);

    // 로그인하지 않은 경우에만 컴포넌트를 렌더링합니다.
    return !isLoggedIn ? <WrappedComponent {...props} /> : null;
  };
};

export default PublicRoute;

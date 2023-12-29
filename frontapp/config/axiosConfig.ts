import { logout } from "@/store/userSlice";
import axios, { AxiosError } from "axios";
import { useRouter } from "next/navigation";
import { useEffect } from "react";
import { useDispatch } from "react-redux";

export const instance = axios.create({
  baseURL: "/api/v1",
  headers: {
    "Content-Type": "application/json",
  },
});

export const Interceptor = ({ children }: any) => {
  const dispatch = useDispatch();
  const router = useRouter();

  useEffect(() => {
    const interceptor = instance.interceptors.response.use(
      (response) => response,
      (error: AxiosError) => {
        if (error.response?.status === 401) {
          dispatch(logout());
          router.push("/login");
          alert("로그인이 만료되었습니다. 다시 로그인해주세요.");
        } else if (error.response?.status === 403) {
          alert("권한이 없습니다.");
        }
        return Promise.reject(error);
      },
    );

    return () => instance.interceptors.response.eject(interceptor);
  }, [router, dispatch]);

  return children;
};

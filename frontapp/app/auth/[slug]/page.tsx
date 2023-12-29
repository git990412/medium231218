"use client";
import { useRouter, useSearchParams } from "next/navigation";
import { useEffect, useState } from "react";
import { Spinner } from "@nextui-org/react";
import { instance } from "@/config/axiosConfig";
import { useDispatch } from "react-redux";
import { login } from "@/store/userSlice";

const Page = ({ params }: { params: { slug: string } }) => {
  const searchParams = useSearchParams();
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const router = useRouter();
  const dispatch = useDispatch();

  useEffect(() => {
    if (params.slug === "naver") {
      const code = searchParams.get("code");
      const state = searchParams.get("state");

      instance
        .post("/members/naver/login", {
          clientId: process.env.NEXT_PUBLIC_NAVER_CLIENT_ID,
          clientSecret: process.env.NEXT_PUBLIC_NAVER_CLIENT_SECRET,
          grantType: "authorization_code",
          code: code,
          state: state,
        })
        .then((res) => {
          dispatch(login(res.data));
          setIsLoading(false);
          alert("로그인에 성공했습니다.");
          router.push("/");
        });
    }
  }, []);
  return (
    <div className={"mt-20 flex justify-center items-center"}>
      <Spinner />
    </div>
  );
};

export default Page;

"use client";

import { instance } from "@/config/axiosConfig";
import { Button, Input } from "@nextui-org/react";
import { ChangeEvent, useState } from "react";
import EmailInput from "../../components/EmailInput";
import PasswordInput from "../../components/PasswordInput";
import { useRouter } from "next/navigation";
import { AxiosError } from "axios";

const Page = () => {
  const router = useRouter();

  const [signupForm, setSignupForm] = useState({
    username: "",
    email: "",
    password: "",
    passwordConfirm: "",
  });

  const [errorData, setErrorData] = useState({
    username: "",
    email: "",
    password: "",
    passwordConfirm: "",
  });

  const handleChange = (e: ChangeEvent<HTMLInputElement>) => {
    setSignupForm({
      ...signupForm,
      [e.target.name]: e.target.value,
    });

    setErrorData({
      username: "",
      email: "",
      password: "",
      passwordConfirm: "",
    });
  };

  const submit = () => {
    instance
      .post("/members", signupForm)
      .then((res) => {
        router.push("/login");
      })
      .catch((err: AxiosError) => {
        setErrorData({
          ...errorData,
          ...(err.response?.data as any),
        });
      });
  };

  return (
    <div className="flex justify-center">
      <div className="w-3/4 max-w-sm flex flex-col items-center">
        <h1 className="text-4xl font-bold mt-20">Sign up</h1>
        <Input
          type="text"
          name="username"
          value={signupForm.username}
          onChange={handleChange}
          isInvalid={errorData.username !== ""}
          errorMessage={errorData.username}
          label="Username"
          variant="bordered"
          className="w-full mt-8"
        />
        <EmailInput
          value={signupForm.email}
          onChange={handleChange}
          className="w-full mt-2"
          isInvalid={errorData.email !== ""}
          errorMessage={errorData.email}
        />
        <PasswordInput
          className="w-full mt-2"
          value={signupForm.password}
          onChange={handleChange}
          isInvalid={errorData.password !== ""}
          errorMessage={errorData.password}
          name="password"
          lable="Password"
        />
        <PasswordInput
          className="w-full mt-2"
          value={signupForm.passwordConfirm}
          onChange={handleChange}
          isInvalid={errorData.passwordConfirm !== ""}
          errorMessage={errorData.passwordConfirm}
          name="passwordConfirm"
          lable="Password Confirm"
        />
        <Button className="w-full mt-2" onClick={submit}>
          회원가입
        </Button>
      </div>
    </div>
  );
};

export default Page;

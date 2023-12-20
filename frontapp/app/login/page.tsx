"use client";
import EmailInput from "@/components/EmailInput";
import PasswordInput from "@/components/PasswordInput";
import { instance } from "@/config/axiosConfig";
import { login } from "@/store/userSlice";
import { Button } from "@nextui-org/react";
import { AxiosError } from "axios";
import { useState } from "react";
import { useDispatch } from "react-redux";

const Page = () => {
  const dispatch = useDispatch();

  const [loginForm, setLoginForm] = useState({
    email: "",
    password: "",
  });

  const [errors, setErrors] = useState({
    email: "",
    password: "",
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setLoginForm({
      ...loginForm,
      [e.target.name]: e.target.value,
    });

    setErrors({
      ...errors,
      [e.target.name]: "",
    });
  };

  const handleSubmit = () => {
    instance
      .post("/members/login", loginForm)
      .then((res) => {
        console.log(res.data);
      })
      .catch((err: AxiosError) => {
        setErrors({
          ...errors,
          ...(err.response?.data as any),
        });
      });
  };

  return (
    <div className="flex justify-center">
      <div className="w-3/4 max-w-sm flex flex-col items-center">
        <h1 className="text-4xl font-bold mt-20">Login</h1>
        <EmailInput
          value={loginForm.email}
          onChange={handleChange}
          className="w-full mt-8"
          isInvalid={errors.email !== ""}
          errorMessage={errors.email}
        />
        <PasswordInput
          className="w-full mt-2"
          value={loginForm.password}
          onChange={handleChange}
          isInvalid={errors.password !== ""}
          errorMessage={errors.password}
          name="password"
          lable="Password"
        />
        <Button className="w-full mt-2" onClick={handleSubmit}>
          로그인
        </Button>
      </div>
    </div>
  );
};

export default Page;

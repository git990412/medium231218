import React from "react";
import { Input } from "@nextui-org/react";
import { EyeFilledIcon } from "./EyeFilledIcon";
import { EyeSlashFilledIcon } from "./EyeSlashFilledIcon";

interface Props {
  className?: string;
  lable?: string;
  onChange?: (e: React.ChangeEvent<HTMLInputElement>) => void;
  value?: string;
  placeholder?: string;
  name?: string;
  isInvalid?: boolean;
  errorMessage?: string;
}

export default function PasswordInput(props: Props) {
  const [isVisible, setIsVisible] = React.useState(false);

  const toggleVisibility = () => setIsVisible(!isVisible);

  return (
    <Input
      label={props.lable}
      variant="bordered"
      placeholder={props.placeholder}
      value={props.value}
      onChange={props.onChange}
      name={props.name}
      isInvalid={props.isInvalid}
      errorMessage={props.errorMessage}
      endContent={
        <button
          className="focus:outline-none"
          type="button"
          onClick={toggleVisibility}
        >
          {isVisible ? (
            <EyeSlashFilledIcon className="text-2xl text-default-400 pointer-events-none" />
          ) : (
            <EyeFilledIcon className="text-2xl text-default-400 pointer-events-none" />
          )}
        </button>
      }
      type={isVisible ? "text" : "password"}
      className={props.className}
    />
  );
}

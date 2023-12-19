import React, { ChangeEvent } from "react";
import { Input } from "@nextui-org/react";

interface Props {
  value?: string;
  onChange: (e: ChangeEvent<HTMLInputElement>) => void;
  className?: string;
  isInvalid?: boolean;
  errorMessage?: string;
}

export default function EmailInput(props: Props) {
  const validateEmail = (value: string | undefined) =>
    value?.match(/^[A-Z0-9._%+-]+@[A-Z0-9.-]+.[A-Z]{2,4}$/i);

  const isInvalid = React.useMemo(() => {
    if (props.value === "") return false;

    return validateEmail(props.value) ? false : true;
  }, [props.value]);

  return (
    <Input
      value={props.value}
      type="email"
      name="email"
      label="Email"
      variant="bordered"
      isInvalid={isInvalid || props.isInvalid}
      errorMessage={
        (isInvalid && "Please enter a valid email") ||
        (props.isInvalid && props.errorMessage)
      }
      onChange={props.onChange}
      className={props.className}
    />
  );
}

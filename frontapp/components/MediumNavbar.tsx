"use client";

import {
  Navbar,
  NavbarContent,
  NavbarMenuToggle,
  NavbarBrand,
  NavbarItem,
  Button,
  NavbarMenu,
  NavbarMenuItem,
} from "@nextui-org/react";
import Link from "next/link";
import React from "react";
import { useDispatch, useSelector } from "react-redux";
import { RootState } from "../store/store";
import { instance } from "@/config/axiosConfig";
import { useRouter } from "next/navigation";
import { logout } from "@/store/userSlice";

const MediumNavbar = () => {
  const [isMenuOpen, setIsMenuOpen] = React.useState(false);
  const isLoggedIn = useSelector((state: RootState) => state.user.isLoggedIn);
  const router = useRouter();
  const dispatch = useDispatch();

  const logoutF = () => {
    // 로그아웃 로직
    instance.post("/members/logout").then(() => {
      dispatch(logout());
      alert("로그아웃 되었습니다.");
      router.replace("/");
    });
  };

  const menuItems = [
    { name: "글 등록", href: "/post/write", permission: "auth" },
    { name: "Sign Up", href: "/signup", permission: "anonymous" },
    { name: "LogIn", href: "/login", permission: "anonymous" },
    { name: "Logout", href: "#", permission: "auth" },
  ];

  // 필터링 로직을 별도의 함수로 분리
  const filteredMenuItems = menuItems.filter(
    (item) =>
      (isLoggedIn && item.permission === "auth") ||
      (!isLoggedIn && item.permission === "anonymous")
  );

  // 메뉴 아이템 렌더링을 위한 컴포넌트
  const MenuItem = ({ item }: any) => (
    <NavbarMenuItem className="text-sm hover:text-blue-600">
      <Link
        className="w-full"
        href={item.href}
        onClick={() => {
          if (item.name === "Logout") logoutF();
          setIsMenuOpen(false);
        }}
      >
        {item.name}
      </Link>
    </NavbarMenuItem>
  );

  return (
    <Navbar
      onMenuOpenChange={setIsMenuOpen}
      isMenuOpen={isMenuOpen}
      className={"drop-shadow-md"}
    >
      <NavbarContent>
        <NavbarMenuToggle
          aria-label={isMenuOpen ? "Close menu" : "Open menu"}
          className="sm:hidden"
        />
        <NavbarBrand>
          <Link href={"/"} className="font-bold text-inherit">
            Medium
          </Link>
        </NavbarBrand>
      </NavbarContent>
      <NavbarContent justify="end">
        {filteredMenuItems.map((item, index) => (
          <MenuItem key={`${item.name}-${index}`} item={item} />
        ))}
      </NavbarContent>
      <NavbarMenu>
        {filteredMenuItems.map((item, index) => (
          <MenuItem key={`${item.name}-${index}`} item={item} />
        ))}
      </NavbarMenu>
    </Navbar>
  );
};

export default MediumNavbar;

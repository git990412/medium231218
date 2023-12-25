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
import React, { use } from "react";
import { useSelector } from "react-redux";
import { RootState } from "../store/store";

const MediumNavbar = () => {
  const [isMenuOpen, setIsMenuOpen] = React.useState(false);
  const isLoggedIn = useSelector((state: RootState) => state.user.isLoggedIn);

  const menuItems = [
    {
      name: "Sign Up",
      href: "/signup",
      permission: "anonymous",
    },
    {
      name: "Log in",
      href: "/login",
      permission: "anonymous",
    },
  ];

  return (
    <Navbar
      onMenuOpenChange={setIsMenuOpen}
      isMenuOpen={isMenuOpen}
      className={"drop-shadow-md"}
    >
      <NavbarContent>
        {/* 모바일 메뉴 토글버튼 */}
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
        {!isLoggedIn && (
          <>
            <NavbarItem className="hidden lg:flex">
              <Link href="/login">Login</Link>
            </NavbarItem>
            <NavbarItem>
              <Button as={Link} href="/signup" variant="flat">
                Sign Up
              </Button>
            </NavbarItem>
          </>
        )}
      </NavbarContent>
      {/* 모바일용 메뉴 */}
      <NavbarMenu>
        {menuItems
          .filter((item) => !isLoggedIn || item.permission !== "anonymous")
          .map((item, index) => (
            <NavbarMenuItem key={`${item}-${index}`}>
              <Link
                className="w-full"
                href={item.href}
                onClick={() => {
                  setIsMenuOpen(false);
                }}
              >
                {item.name}
              </Link>
            </NavbarMenuItem>
          ))}
      </NavbarMenu>
    </Navbar>
  );
};

export default MediumNavbar;

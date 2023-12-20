import type { Metadata } from "next";
import { Inter } from "next/font/google";
import "./globals.css";
import { Providers as NextUiProviders } from "./providers";
import MediumNavbar from "@/components/MediumNavbar";
import { Provider } from "react-redux";
import { store, persistor } from "../store/store";
import { PersistGate } from "redux-persist/integration/react";

const inter = Inter({ subsets: ["latin"] });

export const metadata: Metadata = {
  title: "Medium",
  description: "Generated by create next app",
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="ko">
      <body className={inter.className}>
        <Provider store={store}>
          <PersistGate loading={null} persistor={persistor}>
            <NextUiProviders>
              <MediumNavbar />
              {children}
            </NextUiProviders>
          </PersistGate>
        </Provider>
      </body>
    </html>
  );
}

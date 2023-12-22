"use client";

import { NextUIProvider } from "@nextui-org/react";
import { Provider } from "react-redux";
import { store, persistor } from "../store/store";
import { PersistGate } from "redux-persist/integration/react";
import { Interceptor } from "@/config/axiosConfig";

export function Providers({ children }: { children: React.ReactNode }) {
  return (
    <Provider store={store}>
      <PersistGate loading={null} persistor={persistor}>
        <NextUIProvider>
          <Interceptor>{children}</Interceptor>
        </NextUIProvider>
      </PersistGate>
    </Provider>
  );
}

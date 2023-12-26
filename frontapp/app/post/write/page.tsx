"use client";
import PrivateRoute from "@/components/PrivateRoute";
import TextEditor from "@/components/TextEditor";
import { instance } from "@/config/axiosConfig";
import "@/styles/quillStyle.css";
import { components } from "@/types/api/v1/schema";
import { useRouter } from "next/navigation";
import React, { useState } from "react";
import "react-quill/dist/quill.snow.css";

const Page = () => {
  const router = useRouter();
  const [post, setPost] = useState<components["schemas"]["PostDto"]>({
    published: true,
  });

  return (
    <TextEditor
      post={post}
      setPost={setPost}
      onSubmit={() => {
        instance.post("/posts/write", post).then((res) => {
          alert("등록되었습니다.");
          router.back();
        });
      }}
    />
  );
};

export default PrivateRoute(Page);

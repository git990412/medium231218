"use client";
import TextEditor from "@/components/TextEditor";
import { components } from "@/types/api/v1/schema";
import { useEffect, useState } from "react";
import { instance } from "@/config/axiosConfig";
import PrivateRoute from "@/components/PrivateRoute";
import { useRouter } from "next/navigation";

const Page = ({ params }: { params: { index: string } }) => {
  const [post, setPost] = useState<components["schemas"]["PostDto"]>({});
  const router = useRouter();

  useEffect(() => {
    instance.get(`/posts/${params.index}`).then((res) => {
      setPost(res.data);
    });
  }, []);

  return (
    <TextEditor
      post={post}
      setPost={setPost}
      onSubmit={() => {
        instance.put(`/posts/${params.index}/modify`, post).then((res) => {
          alert("수정되었습니다.");
          router.back();
        });
      }}
    />
  );
};
export default PrivateRoute(Page as any);

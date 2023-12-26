"use client";
import { instance } from "@/config/axiosConfig";
import { RootState } from "@/store/store";
import { components } from "@/types/api/v1/schema";
import { Divider, Button } from "@nextui-org/react";
import DOMPurify from "dompurify";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import "react-quill/dist/quill.snow.css";
import { useSelector } from "react-redux";

const Page = ({ params }: { params: { index: string } }) => {
  const username = useSelector((state: RootState) => state.user.username);
  const router = useRouter();

  useEffect(() => {
    instance.get(`/posts/${params.index}`).then((res) => {
      setPost(res.data);
    });
  }, []);

  const deletePost = () => {
    instance.delete(`/posts/${params.index}/delete`).then((res) => {
      const rsData = res.data;

      if (rsData.success) {
        alert("삭제되었습니다.");
        router.replace("/");
      }
    });
  };

  const [post, setPost] = useState<components["schemas"]["PostDto"]>({});

  return (
    <div>
      <h1 className="font-bold text-2xl">{post.title}</h1>
      <Divider className="mt-2" />
      <div className="ql-snow">
        <div className="ql-editor">
          <div
            dangerouslySetInnerHTML={{
              __html: DOMPurify.sanitize(String(post?.body)),
            }}
          ></div>
        </div>
      </div>

      {username === post.member?.username ? (
        <Button
          className="mt-2 float-right"
          as={Link}
          href={`/post/${post.id}/modify`}
          color="primary"
        >
          수정
        </Button>
      ) : null}
      {username === post.member?.username ? (
        <Button
          className="mt-2 mr-2 float-right"
          onClick={deletePost}
          color="primary"
        >
          삭제
        </Button>
      ) : null}
    </div>
  );
};

export default Page;

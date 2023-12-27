"use client";
import { instance } from "@/config/axiosConfig";
import { RootState } from "@/store/store";
import { components } from "@/types/api/v1/schema";
import {
  Badge,
  Button,
  Card,
  CardBody,
  CardHeader,
  Divider,
  Textarea,
  User,
} from "@nextui-org/react";
import DOMPurify from "dompurify";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import "react-quill/dist/quill.snow.css";
import { useSelector } from "react-redux";

const CommentArea = (props: { index: string; loadPost: () => void }) => {
  const [comments, setComments] = useState<string>("");

  const onSubmit = () => {
    instance
      .post(`/posts/${props.index}/comments/write`, {
        body: comments,
      })
      .then((res) => {
        alert("댓글이 등록되었습니다.");
        setComments("");
        props.loadPost();
      });
  };
  return (
    <>
      <Textarea
        variant="faded"
        label="댓글"
        value={comments}
        onValueChange={setComments}
        placeholder="내용을 입력해주세요."
        className="w-full mt-5"
      />
      <Button onClick={onSubmit} className="w-full mt-2" color={"primary"}>
        등록
      </Button>
    </>
  );
};

const CommentBox = (props: {
  comment: components["schemas"]["PostCommentDto"];
  index: string;
  loadPost?: () => void;
}) => {
  const comment = props.comment;
  const username = useSelector((state: RootState) => state.user.username);

  const [isModify, setIsModify] = useState<boolean>(false);
  const [value, setValue] = useState<string>(comment.body ?? "");

  const loadComment = () => {
    instance.get(`/posts/${props.index}/comments/${comment.id}`).then((res) => {
      setValue(res.data.body);
    });
  };

  const toggleModify = () => {
    if (isModify) {
      instance
        .put(`/posts/${props.index}/comments/${comment.id}/modify`, {
          body: value,
        })
        .then((res) => {
          alert("수정되었습니다.");
          loadComment();
          setIsModify(false);
        });
    } else {
      setIsModify(true);
    }
  };

  const deleteComment = () => {
    instance
      .delete(`/posts/${props.index}/comments/${comment.id}/delete`)
      .then((res) => {
        alert("삭제되었습니다.");
        props.loadPost?.();
      });
  };

  return (
    <Card key={comment.id} className="w-full mt-4">
      <CardHeader className="flex gap-3 justify-between">
        <User name={comment.username} />
        {username === comment.username && (
          <div className={"flex"}>
            <div
              className={"hover:cursor-pointer hover:text-blue-600"}
              onClick={() => toggleModify()}
            >
              {isModify ? "완료" : "수정"}
            </div>
            <div className={"px-2"}>|</div>
            <div
              className={"hover:cursor-pointer hover:text-blue-600"}
              onClick={deleteComment}
            >
              삭제
            </div>
          </div>
        )}
      </CardHeader>
      <Divider />
      <CardBody>
        {isModify ? (
          <Textarea
            variant={"underlined"}
            value={value}
            onValueChange={setValue}
          />
        ) : (
          <p>{value}</p>
        )}
      </CardBody>
    </Card>
  );
};

const Page = ({ params }: { params: { index: string } }) => {
  const username = useSelector((state: RootState) => state.user.username);
  const router = useRouter();

  const loadPost = () => {
    instance.get(`/posts/${params.index}`).then((res) => {
      setPost(res.data);
    });
  };

  useEffect(() => {
    loadPost();

    instance.put(`/posts/${params.index}/hit`);
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

  const addLike = () => {
    instance
      .post(`/posts/${params.index}/like`)
      .then((res) => {
        alert("추천되었습니다.");
        loadPost();
      })
      .catch(() => {
        const result = confirm("이미 추천하셨습니다. 추천을 취소하시겠습니까?");
        if (result) {
          instance.delete(`/posts/${params.index}/cancelLike`).then((res) => {
            alert("추천이 취소되었습니다.");
            loadPost();
          });
        }
      });
  };

  const [post, setPost] = useState<components["schemas"]["PostDto"]>({});

  return (
    <div className="mt-10">
      <div className="flex justify-between items-center">
        <h1 className="font-bold text-2xl">{post.title}</h1>
        {username === post.member?.username ? (
          <div className="flex">
            <Link
              href={`/post/${post.id}/modify`}
              className="hover:cursor-pointer hover:text-blue-600"
            >
              수정
            </Link>
            <div className="px-2">|</div>
            <div
              onClick={deletePost}
              className="hover:cursor-pointer hover:text-blue-600"
            >
              삭제
            </div>
          </div>
        ) : null}
      </div>
      <Divider className="mt-2" />
      <div className="ql-snow">
        <div className="ql-editor !p-0 !py-5">
          <div
            dangerouslySetInnerHTML={{
              __html: DOMPurify.sanitize(String(post?.body)),
            }}
          ></div>
        </div>
      </div>
      <div className={"flex justify-center"}>
        <Badge content={post.likes ?? 0} color={"primary"}>
          <Button onClick={addLike}>추천</Button>
        </Badge>
      </div>
      <Divider className={"mt-2"} />
      {post.comments?.map((comment) => (
        <CommentBox
          comment={comment}
          index={params.index}
          loadPost={loadPost}
        />
      ))}
      <CommentArea index={params.index} loadPost={loadPost} />
    </div>
  );
};

export default Page;

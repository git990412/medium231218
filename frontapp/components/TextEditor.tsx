"use client";
import PrivateRoute from "@/components/PrivateRoute";
import { instance } from "@/config/axiosConfig";
import "@/styles/quillStyle.css";
import { components } from "@/types/api/v1/schema";
import { Button } from "@nextui-org/button";
import { Input } from "@nextui-org/input";
import { Checkbox } from "@nextui-org/react";
import { useRouter } from "next/navigation";
import React, { useMemo, useState } from "react";
import ReactQuill from "react-quill";
import "react-quill/dist/quill.snow.css";

interface TextEditorProps {
  post: components["schemas"]["PostDto"];
  setPost: React.Dispatch<
    React.SetStateAction<components["schemas"]["PostDto"]>
  >;
  onSubmit?: () => void;
}

const TextEditor = (props: TextEditorProps) => {
  const router = useRouter();

  const quillRef = React.useRef<ReactQuill>(null);
  const imageHandler = async () => {
    // file input 임의 생성
    const input = document.createElement("input");
    input.setAttribute("type", "file");
    input.click();

    input.onchange = async () => {
      const file = input.files;
      const formData = new FormData();

      if (file) {
        formData.append("file", file[0]);
      }

      // file 데이터 담아서 서버에 전달하여 이미지 업로드
      const res = await instance.post("/posts/imageUpload", formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      });

      if (quillRef.current) {
        // 현재 Editor 커서 위치에 서버로부터 전달받은 이미지 불러오는 url을 이용하여 이미지 태그 추가
        const index = quillRef.current.getEditor().getSelection()?.index;

        const quillEditor = quillRef.current.getEditor();

        if (index === null || index === undefined) return;
        quillEditor.setSelection(index, 1);

        quillEditor.clipboard.dangerouslyPasteHTML(
          index,
          `<img src=${res.data} alt=${"alt text"} />`
        );
      }
    };
  };

  const modules = useMemo(
    () => ({
      toolbar: {
        container: [
          ["bold", "italic", "underline", "strike"],
          ["blockquote", "code-block"],

          [{ header: 1 }, { header: 2 }],
          [{ list: "ordered" }, { list: "bullet" }],
          [{ script: "sub" }, { script: "super" }],
          [{ indent: "-1" }, { indent: "+1" }],
          [{ direction: "rtl" }],

          [{ size: ["small", false, "large", "huge"] }],
          [{ header: [1, 2, 3, 4, 5, 6, false] }],

          [{ color: [] }, { background: [] }],
          [{ font: [] }],
          [{ align: [] }],

          ["clean"],
          ["image"],
        ],
        handlers: {
          image: imageHandler,
        },
      },
    }),
    []
  );

  return (
    <div>
      <Input
        type="text"
        classNames={{
          input: ["text-2xl"],
          inputWrapper: ["mb-2"],
        }}
        placeholder="제목을 입력해주세요"
        variant={"underlined"}
        value={props.post.title}
        onChange={(e) => {
          props.setPost({ ...props.post, title: e.target.value });
        }}
      />
      <Checkbox
        name="published"
        isSelected={props.post.published}
        onChange={(e) => {
          props.setPost({ ...props.post, published: e.target.checked });
        }}
        defaultSelected
      >
        공개
      </Checkbox>
      <ReactQuill
        className="mt-2"
        value={props.post.body}
        ref={quillRef}
        modules={modules}
        theme="snow"
        onChange={(v) => {
          props.setPost({ ...props.post, body: v });
        }}
      />
      <Button className="w-full mt-2" onClick={props.onSubmit} color="primary">
        등록하기
      </Button>
    </div>
  );
};

export default TextEditor;

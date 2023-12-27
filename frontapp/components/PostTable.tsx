"use client";
import { instance } from "@/config/axiosConfig";
import {
  getKeyValue,
  Input,
  Pagination,
  Select,
  SelectItem,
  Spacer,
  Spinner,
  Table,
  TableBody,
  TableCell,
  TableColumn,
  TableHeader,
  TableRow,
} from "@nextui-org/react";
import React, { useMemo, useState } from "react";
import useSWR from "swr";
import { components } from "@/types/api/v1/schema";
import dateStringFormatter from "@/util/dateStringFormatter";
import { useRouter } from "next/navigation";

// fetcher 함수의 타입을 정의합니다.
const fetcher = (url: string) => instance.get(url).then((res) => res.data);

interface PostTableProps {
  page?: number;
  url?: string;
  className?: string;
}

export default function PostTable(props: PostTableProps) {
  const router = useRouter();
  const [page, setPage] = useState<number>(1);
  const [url, setUrl] = useState<string>(
    `${props.url ?? "posts/list"}?page=${page - 1}`,
  );

  const { data, isValidating: isLoading } = useSWR<
    components["schemas"]["PagePostDto"]
  >(url, fetcher, {
    keepPreviousData: true,
  });

  const pages = useMemo(() => {
    return props.page ?? data?.totalPages ?? 0;
  }, [data?.totalElements]);

  const loadingState =
    isLoading || data?.totalElements === 0 ? "loading" : "idle";

  return (
    <>
      <Table
        aria-label="Example table with client async pagination"
        className={props.className}
        bottomContent={
          pages > 0 && (
            <div className="flex w-full justify-center">
              <Pagination
                isCompact
                showControls
                showShadow
                color="primary"
                page={page}
                total={pages}
                onChange={(page) => setPage(page)}
              />
            </div>
          )
        }
      >
        <TableHeader>
          <TableColumn key="title">제목</TableColumn>
          <TableColumn key="createDate">작성일</TableColumn>
          <TableColumn key="username">작성자</TableColumn>
          <TableColumn key="hit">조회수</TableColumn>
        </TableHeader>
        <TableBody
          items={data?.content ?? []}
          loadingContent={<Spinner />}
          loadingState={loadingState}
        >
          {(item: components["schemas"]["PostDto"]) => (
            <TableRow key={item.id}>
              {(columnKey) => {
                if (columnKey === "username") {
                  return <TableCell>{item.member?.username}</TableCell>;
                } else if (columnKey === "createDate") {
                  return (
                    <TableCell>
                      {dateStringFormatter(item?.createDate)}
                    </TableCell>
                  );
                } else if (columnKey === "title") {
                  return (
                    <TableCell
                      className="hover:cursor-pointer hover:text-blue-600"
                      onClick={() => {
                        router.push(`/post/${item.id}`);
                      }}
                    >
                      {item.title}
                    </TableCell>
                  );
                }
                return <TableCell>{getKeyValue(item, columnKey)}</TableCell>;
              }}
            </TableRow>
          )}
        </TableBody>
      </Table>
      <Spacer y={1} />
      <form
        onSubmit={(e) => {
          e.preventDefault();
          const formData = new FormData(e.target as HTMLFormElement);
          const searchType = formData.get("searchType") as string;
          const searchKeyword = formData.get("searchKeyword") as string;
          setUrl(
            `posts/list?page=${
              page - 1
            }&kwType=${searchType}&kw=${searchKeyword}`,
          );
        }}
        className={"w-3/4 flex"}
      >
        <Select
          variant={"underlined"}
          className={"w-2/5"}
          size={"sm"}
          name={"searchType"}
          defaultSelectedKeys={["title"]}
        >
          <SelectItem key={"title"}>제목</SelectItem>
          <SelectItem key={"body"}>내용</SelectItem>
          <SelectItem key={"title,body"}>제목,내용</SelectItem>
        </Select>
        <Spacer />
        <Input
          variant={"underlined"}
          name={"searchKeyword"}
          size={"sm"}
          type={"search"}
          placeholder={"검색어를 입력해주세요."}
        />
        <input type="submit" style={{ display: "none" }} />
      </form>
    </>
  );
}

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

  const [search, setSearch] = useState<{
    sortCode: string;
    kwType: string;
    kw: string;
  }>({
    sortCode: "idDesc",
    kwType: "",
    kw: "",
  });

  const { data, isValidating: isLoading } = useSWR<
    components["schemas"]["PagePostDto"]
  >(
    `${props.url ?? "posts/list"}?page=${page - 1}&sortCode=${
      search.sortCode
    }&kwType=${search.kwType}&kw=${search.kw}`,
    fetcher,
    {
      keepPreviousData: true,
    },
  );

  const pages = useMemo(() => {
    return props.page ?? data?.totalPages ?? 0;
  }, [data?.totalElements]);

  const loadingState =
    isLoading || data?.totalElements === 0 ? "loading" : "idle";

  return (
    <>
      <div className={props.className + " flex w-full"}>
        <div className={"flex w-2/5"}>
          <Select
            variant={"underlined"}
            defaultSelectedKeys={["idDesc"]}
            label={"보기"}
            labelPlacement={"outside-left"}
            value={search.sortCode}
            onChange={(e) => {
              setSearch({ ...search, sortCode: e.target.value });
            }}
            classNames={{ label: "w-12", base: "flex items-center" }}
          >
            <SelectItem key={"idDesc"}>id최신순</SelectItem>
            <SelectItem key={"idAsc"}>id오래된순</SelectItem>
            <SelectItem key={"hitDesc"}>조회수높은순</SelectItem>
            <SelectItem key={"likeCountAsc"}>추천수낮은순</SelectItem>
          </Select>
        </div>
      </div>
      <Table
        aria-label="Example table with client async pagination"
        className={"mt-2"}
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
                onChange={(page) => {
                  setPage(page);
                }}
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
          const searchType = formData.getAll("searchType").join(",");
          const searchKeyword = formData.get("searchKeyword") as string;
          setSearch({ ...search, kwType: searchType, kw: searchKeyword });
        }}
        className={"w-3/4 flex"}
      >
        <Select
          aria-label={"검색 타입"}
          variant={"underlined"}
          className={"w-2/5"}
          selectionMode={"multiple"}
          size={"sm"}
          name={"searchType"}
          defaultSelectedKeys={["title"]}
        >
          <SelectItem classNames={{ title: "text-xs" }} key={"title"}>
            제목
          </SelectItem>
          <SelectItem classNames={{ title: "text-xs" }} key={"body"}>
            내용
          </SelectItem>
          <SelectItem classNames={{ title: "text-xs" }} key={"author"}>
            닉네임
          </SelectItem>
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

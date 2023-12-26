"use client";
import { instance } from "@/config/axiosConfig";
import {
  Button,
  Pagination,
  Spinner,
  Table,
  TableBody,
  TableCell,
  TableColumn,
  TableHeader,
  TableRow,
  getKeyValue,
} from "@nextui-org/react";
import { useState, useMemo } from "react";
import useSWR from "swr";
import { components } from "@/types/api/v1/schema";
import dateStringFormatter from "@/util/dateStringFormatter";
import { useRouter } from "next/navigation";

// fetcher 함수의 타입을 정의합니다.
const fetcher = (url: string) => instance.get(url).then((res) => res.data);

interface PostTableProps {
  page?: number;
  url?: string;
}

export default function PostTable(props: PostTableProps) {
  const router = useRouter();
  const [page, setPage] = useState<number>(1);

  const { data, isValidating: isLoading } = useSWR<
    components["schemas"]["PagePostDto"]
  >(`${props.url ?? "posts/list"}?page=${page - 1}`, fetcher, {
    keepPreviousData: true,
  });

  const pages = useMemo(() => {
    return props.page ?? data?.totalPages ?? 0;
  }, [data?.totalElements]);

  const loadingState =
    isLoading || data?.totalElements === 0 ? "loading" : "idle";

  return (
    <Table
      aria-label="Example table with client async pagination"
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
                  <TableCell>{dateStringFormatter(item?.createDate)}</TableCell>
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
  );
}

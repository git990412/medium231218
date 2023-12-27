"use client";
import PostTable from "@/components/PostTable";

export default function Home() {
  return (
    <div className="flex flex-col items-center">
      <h1 className="text-4xl font-bold mt-10">최신 글</h1>
      <PostTable className="mt-10" page={3} />
    </div>
  );
}

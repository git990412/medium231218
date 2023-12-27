import PostTable from "@/components/PostTable";

const Page = () => {
  return (
    <div className="flex flex-col items-center">
      <h1 className="text-4xl font-bold mt-10">전체 글</h1>
      <PostTable className="mt-10" />
    </div>
  );
};

export default Page;

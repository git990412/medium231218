import moment from "moment";

export default function dateStringFormatter(
  dateString: string | undefined
): string {
  const date = moment(dateString);
  const now = moment();

  const diffMinutes = now.diff(date, "minutes");
  const diffHours = now.diff(date, "hours");
  const diffDays = now.diff(date, "days");
  const diffMonths = now.diff(date, "months");
  const diffYears = now.diff(date, "years");

  if (diffMinutes < 60) {
    return `${diffMinutes} 분전`;
  } else if (diffHours < 24) {
    return `${diffHours} 시간전`;
  } else if (diffDays < 30) {
    return `${diffDays} 일전`;
  } else if (diffMonths < 12) {
    return `${diffMonths} 개월전`;
  } else {
    return `${diffYears} 년전`;
  }
}

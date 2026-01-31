import React from "react";
import { useOverflowTooltip } from "../hooks/useOverflowTooltip";

type Props = React.TdHTMLAttributes<HTMLTableCellElement>;

export const EllipsisTd: React.FC<Props> = ({ children, className = "", ...rest }) => {
  const ref = useOverflowTooltip<HTMLTableCellElement>();

  return (
    <td
      ref={ref}
      className={`ellipsis ${className}`}
      {...rest}
    >
      {children}
    </td>
  );
};

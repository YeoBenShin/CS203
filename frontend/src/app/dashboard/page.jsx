"use client";

import { useEffect, useState } from "react";
import { DataTable } from "./data-table";
import { columns } from "./columns";

export default function DashboardPage() {
    const [data, setData] = useState([]);

    useEffect(() => {
        fetch("http://localhost:8080/api/tariffs/batch?page=1")
        .then((res) => res.json())
        .then((json) => {
            setData(json);
        })
    }, [])
    return (
        <div className="container mx-auto py-10">
            <DataTable columns={columns} data={data} />
        </div>
    )
}
import { Category } from "./category";
import { Status } from "./status";

export interface Task {
    id: number;
    title: string;
    description: string;
    category: Category;
    status: Status;
}
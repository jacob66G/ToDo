export interface Task {
    id: number;
    title: string;
    description: string;
    categoryName: string;
    statusName: string;
}

export interface BodyTaskAdd {
    title: string;
    description: string;
    categoryId: number;
}

export interface BodyTaskEdit {
    title: string;
    description: string;
    categoryId: number;
    statusId: number;
}
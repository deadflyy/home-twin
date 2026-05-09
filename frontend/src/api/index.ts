import { get, post, put, del } from '@/utils/request'

export interface Room {
  id: string
  name: string
  icon: string
  color: string
  itemCount: number
}

export interface Person {
  id: string
  name: string
  relation: string
  avatar: string
  itemCount: number
}

export interface Item {
  id: string
  name: string
  description: string
  photo: string
  roomId: string
  roomName: string
  personId: string
  personName: string
  location: string
  status: string
  addedAt: string
  cleanedAt: string
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  currentPage: number
}

export interface Statistics {
  totalItems: number
  activeItems: number
  cleanedItems: number
  roomStats: Record<string, number>
  personStats: Record<string, number>
}

export async function getRooms(): Promise<Room[]> {
  const res = await get<Room[]>('/rooms')
  return res.data
}

export async function getRoomById(id: string): Promise<Room> {
  const res = await get<Room>(`/rooms/${id}`)
  return res.data
}

export async function getPersons(): Promise<Person[]> {
  const res = await get<Person[]>('/persons')
  return res.data
}

export async function getPersonById(id: string): Promise<Person> {
  const res = await get<Person>(`/persons/${id}`)
  return res.data
}

export async function getItems(
  roomId?: string,
  personId?: string,
  page = 0,
  size = 10
): Promise<PageResponse<Item>> {
  const params: Record<string, unknown> = { page, size }
  if (roomId) params.roomId = roomId
  if (personId) params.personId = personId
  const res = await get<PageResponse<Item>>('/items', params)
  return res.data
}

export async function getItemById(id: string): Promise<Item> {
  const res = await get<Item>(`/items/${id}`)
  return res.data
}

export async function createItem(data: {
  name: string
  description?: string
  roomId: string
  personId?: string
  location?: string
  photo?: string
}): Promise<Item> {
  const res = await post<Item>('/items', data)
  return res.data
}

export async function updateItem(
  id: string,
  data: {
    name?: string
    description?: string
    roomId?: string
    personId?: string
    location?: string
    photo?: string
  }
): Promise<Item> {
  const res = await put<Item>(`/items/${id}`, data)
  return res.data
}

export async function deleteItem(id: string): Promise<void> {
  await del(`/items/${id}`)
}

export async function cleanItem(id: string): Promise<Item> {
  const res = await put<Item>(`/items/${id}/clean`)
  return res.data
}

export async function searchItems(
  keyword: string,
  roomId?: string,
  page = 0,
  size = 10
): Promise<PageResponse<Item>> {
  const params: Record<string, unknown> = { keyword, page, size }
  if (roomId) params.roomId = roomId
  const res = await get<PageResponse<Item>>('/items/search', params)
  return res.data
}

export async function getStatistics(): Promise<Statistics> {
  const res = await get<Statistics>('/items/statistics')
  return res.data
}

import {ProductStatus} from '../Enum/ProductStatus';

export interface ProductInfo{

sku:string;
title:string;
description:string;
price:number;
status:ProductStatus;
categories:string[];

}

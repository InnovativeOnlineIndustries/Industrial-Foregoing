package com.buuz135.industrial.book;


import com.buuz135.industrial.api.book.IPage;

import java.util.List;

public interface IHasBookDescription {

    List<IPage> getBookDescriptionPages();

    BookCategory getCategory();
}

package org.duckdns.massemiso.todo_list_api.dto;

import jakarta.validation.constraints.NotEmpty;

public record TodoRequestDto(
    @NotEmpty(message = "must not be empty") String title,
    @NotEmpty(message = "must not be empty") String description,
    Boolean completed
) {
  @Override
  public Boolean completed(){
    if (this.completed == null){
      return false;
    }
    return this.completed;
  }

}

#pragma once

#include <basalt/server.hpp>

struct Buffer {
    char data[96] = {};
    int length = 0;

    Buffer& add(const char* text) {
        while (*text != 0 && length < 95) {
            data[length++] = *text++;
        }
        return *this;
    }

    Buffer& add(long long number) {
        char digits[24];
        basalt::server::core::format_i64(number, digits);
        return add(digits);
    }

    const char* c_str() const { return data; }
};
